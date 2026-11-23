# 1. Sistema base: Debian estable (lo pide la rúbrica)
FROM debian:stable

# 2. Como administrador: instalar Java y herramientas, y crear el usuario "usuario"
USER root
RUN apt update \
    && apt -y install --no-install-recommends openjdk-21-jre-headless wget unzip ca-certificates \
    && rm -rf /var/lib/apt/lists/* \
    && useradd -m usuario

# 3. Lo demás se hace como "usuario", dentro de /home/usuario
USER usuario
WORKDIR /home/usuario

# 4. Descargar Open Liberty, descomprimirlo y crear el servidor "cafefe"
RUN wget -q https://repo1.maven.org/maven2/io/openliberty/openliberty-runtime/26.0.0.8/openliberty-runtime-26.0.0.8.zip -O liberty.zip \
    && unzip -q liberty.zip \
    && rm liberty.zip \
    && ./wlp/bin/server create cafefe

# 5. Copiar las librerías "provided" (PrimeFaces y el driver de PostgreSQL)
COPY --chown=usuario:usuario primefaces-15.0.17-jakarta.jar postgresql-42.7.4.jar wlp/usr/shared/config/lib/

# 6. Copiar la configuración del servidor
COPY --chown=usuario:usuario server.xml wlp/usr/servers/cafefe/server.xml

# 7. Idioma por defecto de Java: español (El Salvador), para que las páginas JSF salgan en español
RUN printf -- "-Duser.language=es\n-Duser.country=SV\n" > wlp/usr/servers/cafefe/jvm.options

# 8. Dónde está Java (quita el aviso CWWKE0097W)
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV LANG=C.UTF-8

# 9. Puerto donde escucha Liberty
EXPOSE 9080

# 10. Lo que se ejecuta al encender el contenedor
CMD ["/home/usuario/wlp/bin/server", "run", "cafefe"]
