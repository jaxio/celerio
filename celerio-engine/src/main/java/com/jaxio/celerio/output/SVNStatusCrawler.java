/*
 * Copyright 2015 JAXIO http://www.jaxio.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jaxio.celerio.output;

import lombok.extern.slf4j.Slf4j;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.wc.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SVNStatusCrawler {

    public static boolean isProjectUnderSvn(File baseDir) {
        File svnDirHack = new File(baseDir, ".celerio-please-ignore-svn");
        if (svnDirHack.exists()) {
            return false;
        }

        File svnDir = new File(baseDir, ".svn");
        return svnDir.exists() && svnDir.isDirectory();
    }

    public static SCMStatus doStatus(File baseDir) throws RuntimeException {
        // initialize SVNKit to work through file:/// protocol
        FSRepositoryFactory.setup();

        SVNClientManager clientManager = SVNClientManager.newInstance();
        SVNStatusClient statusClient = clientManager.getStatusClient();
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        ISVNStatusHandler handler = new StatusHandler(baseDir, map);

        try {
            statusClient.doStatus(baseDir,
                SVNRevision.WORKING,
                SVNDepth.INFINITY,
                false /* remote */,
                true /* reportAll */,
                false /* includeIgnored */,
                false /* collectParentExternals */,
                handler, null);
        } catch (SVNException svne) {
            throw new RuntimeException(svne);
        }

        int counter = 0;
        for (Boolean b : map.values()) {
            if (b == Boolean.TRUE) {
                counter++;
            }
        }

        log.info("-----------------------------------------------------------------------------------------------");
        log.info("PROJECT IS UNDER SVN: Files tracked by svn ({}) won't be overwritten/deleted by Celerio", counter);
        log.info("-----------------------------------------------------------------------------------------------");

        return new SCMStatus(map);
    }

    private static class StatusHandler implements ISVNStatusHandler {
        Map<String, Boolean> map;
        String baseDirAbsolutePath;

        public StatusHandler(File baseDir, Map<String, Boolean> map) {
            this.map = map;
            this.baseDirAbsolutePath = baseDir.getAbsolutePath();
        }

        public void handleStatus(SVNStatus status) throws SVNException {
            String relativePath = FileUtil.getPathRelativeToBase(status.getFile(), baseDirAbsolutePath);

            if (status.getContentsStatus().getID() == SVNStatusType.STATUS_NONE.getID() ||
                status.getContentsStatus().getID() == SVNStatusType.STATUS_UNVERSIONED.getID()) {
                map.put(relativePath, Boolean.FALSE);
            } else {
                map.put(relativePath, Boolean.TRUE);
            }
        }
    }
}