FROM adoptopenjdk/maven-openjdk11-openj9:latest

RUN apt-get update -y && apt-get install -y git && git clone https://github.com/bndao/search-cli-poc.git /root/search-cli-poc

COPY start_docker_process.sh /usr/local/bin/

WORKDIR /root/search-cli-poc

EXPOSE 4000

VOLUME [ "/root/search-cli-poc" ]

ENTRYPOINT [ "/bin/bash" ]

CMD ["start_docker_process.sh"]