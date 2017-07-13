## How to make a Release

### GPG key

You need a GPG key.
On mac, you can use https://gpgtools.org/

### Sona type account

You need a Sona Type account
https://issues.sonatype.org/

### Access Github with SSH key

Set up your key
https://help.github.com/articles/generating-ssh-keys/

Once created, you may need to add it manually:
ssh-add ~/.ssh/your_ssh_key

### When cloning the first time

Make sure your project was cloned using:
git clone git@github.com:jaxio/celerio.git

### Releasing

***Make sure BootstrapMojo uses the right packs***

**Make sure JavaDoc is OK**

    mvn javadoc:javadoc
    
**Make sure integration tests pass**

    mvn integration-test -Pit

**Make sure all is committed**

Then execute:

    mvn -Dgpg.passphrase=* release:prepare release:perform

### On OSS site

Go to https://oss.sonatype.org/ and select staging repositories

Find the Jaxio's one (most likely at the end), select it and press the close button

Then press the release button.

Then check that it is present under:
https://oss.sonatype.org/content/repositories/public/com/jaxio/


### Post release

Increment the version manually in

`celerio-maven/bootstrap-maven-plugin/src/it/pom.xml`

(would be nice if it was updated during release...)


That's all, your are done.



