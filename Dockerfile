FROM java:8-jre-alpine

RUN apk add --no-cache git openssh && \
    mkdir /development

# GIT
COPY docker/repo-key /
RUN chmod 600 /repo-key && \  
    echo "IdentityFile /repo-key" >> /etc/ssh/ssh_config && \  
    echo -e "StrictHostKeyChecking no" >> /etc/ssh/ssh_config && \
    git clone git@github.com:elleFlorio/scalachain.git

# SBT
ARG SBT_VERSION
RUN apk add --no-cache bash && \
    apk add --no-cache --virtual=build-dependencies curl && \
    curl -sL "https://piccolo.link/sbt-$SBT_VERSION.tgz" | gunzip | tar -x -C /usr/local && \
    ln -s /usr/local/sbt/bin/sbt /usr/local/bin/sbt && \
    chmod 0755 /usr/local/bin/sbt && \
    apk del build-dependencies

RUN cd /development && \
    sbt sbtVersion

COPY docker/init.sh /etc/init.sh

RUN chmod +x /etc/init.sh

ENTRYPOINT [ "/etc/init.sh" ]

EXPOSE 8080