# Dockerfile

FROM  ubuntu:14.04

MAINTAINER  Eugene Tseytlin <tseytlin@pitt.edu>

ENV JAVA_VER 8
ENV JAVA_HOME /usr/lib/jvm/java-8-oracle

RUN echo 'deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main' >> /etc/apt/sources.list && \
    echo 'deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main' >> /etc/apt/sources.list && \
    apt-key adv --keyserver keyserver.ubuntu.com --recv-keys C2518248EEA14886 && \
    apt-get update && \
    echo oracle-java${JAVA_VER}-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections && \
    apt-get install -y --force-yes --no-install-recommends oracle-java${JAVA_VER}-installer oracle-java${JAVA_VER}-set-default && \
    apt-get clean && \
    rm -rf /var/cache/oracle-jdk${JAVA_VER}-installer
    
RUN update-java-alternatives -s java-8-oracle
RUN echo "export JAVA_HOME=/usr/lib/jvm/java-8-oracle" >> ~/.bashrc
RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

ADD ../../target/BiRADS_Extractor.jar /usr/local/lib/
RUN echo '#!/bin/bash' >> /usr/local/bin/BiRADS_extractor && \
	echo 'java -jar /usr/local/lib/BiRADS_Extractor.jar'  >> /usr/local/bin/BiRADS_extractor  && \
	chmod 755 /usr/local/bin/BiRADS_extractor && \
	mkdir -p /input/text /output
	
CMD	["/usr/local/bin/BiRADS_extractor"]
