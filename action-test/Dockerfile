FROM gradle:jdk8

LABEL "com.github.actions.name"="Test"
LABEL "com.github.actions.description"="Run unit tests"
LABEL "com.github.actions.icon"="code"
LABEL "com.github.actions.color"="blue"

LABEL "repository"="http://github.com/SpiderStrategies/db-import-utils"
LABEL "maintainer"="Jason McKim <jason.mckim@spiderstrategies.com>"

USER root

ADD entrypoint.sh /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]