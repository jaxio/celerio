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

package com.jaxio.celerio.util;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class EncodingUtil {
    private static List<String> isoAccents = newArrayList("¡", "¢", "£", "¤", "¥", "¦", "§", "¨", "©", "ª", "«", "¬", "&", "®", ",", "¯", "°", "±", "²", "³",
            "´", "µ", "¶", "·", "¸", "¹", "º", "»", "¼", "½", "¾", "¿", "×", "÷", "À", "Á", "Â", "Ã", "Ä", "Å", "Æ", "Ç", "È", "É", "Ê", "Ë", "Ì", "Í", "Î",
            "Ï", "Ð", "Ñ", "Ò", "Ó", "Ô", "Õ", "Ö", "Ø", "Ù", "Ú", "Û", "Ü", "Ý", "Þ", "ß", "à", "á", "â", "ã", "ä", "å", "æ", "ç", "è", "é", "ê", "ë", "ì",
            "í", "î", "ï", "ð", "ñ", "ò", "ó", "ô", "õ", "ö", "ø", "ù", "ú", "û", "ü", "ý", "þ", "ÿ");

    private static List<String> isoInUtf8 = buildErrors(isoAccents);

    public static String fixIso(String original) {
        if (original == null) {
            return original;
        }
        for (String iso : isoInUtf8) {
            if (original.contains(iso)) {
                try {
                    return new String(original.getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return original;
    }

    private static List<String> buildErrors(List<String> isoAccents) {
        List<String> ret = newArrayList();
        for (String isoAccent : isoAccents) {
            try {
                ret.add(new String(isoAccent.getBytes(), "ISO-8859-1"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
}
