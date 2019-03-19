
# (Q2) Manuel de déploiement de Weka en Docker

On commence par compiler le WAR avec Maven avec la commande suivante..

```mvn clean package```

On construit ensuite l'image Docker avec la commande suivante.

```docker build -t {username Docker}/jguweka:OAS3 .```

où {username Docker} est le nom d'utilisateur d'un compte Docker (ex.: `docker build -t hacene/jguweka:OAS3 .`)

On télécharge le conteneur Mongo officiel avec la commande qui suit.

```docker pull mongo```

On lance la conteneur Mongo avec la commande suivante.

```docker run --name mongodb -d mongo```

Finalement, on lance l'application Weka REST sur un conteneur avec la commande qui suit.

```docker run -p 8080:8080 --link mongodb:mongodb {username Docker}/jguweka:OAS3```

où {username Docker} est le nom d'utilisateur d'un compte Docker (ex.: `docker run -p 8080:8080 --link mongodb:mongodb hacene/jguweka:OAS3`)

On peut confirmer que le déploiement a été effectué avec succès en naviguant à l'adresse `http://0.0.0.0:8080` sur un fureteur web.

---

Les lignes suivantes ont été ajouté au Dockerfile pour installer JProfiler:

```
RUN wget http://download-keycdn.ej-technologies.com/jprofiler/jprofiler_linux_9_2.tar.gz -P /tmp/ &&\
 tar -xzf /tmp/jprofiler_linux_9_2.tar.gz -C /usr/local &&\
 rm /tmp/jprofiler_linux_9_2.tar.gz
 
ENV JPAGENT_PATH="-agentpath:/usr/local/jprofiler9/bin/linux-x64/libjprofilerti.so=nowait"
EXPOSE 8849
```

Il faut ensuite rebuild le conteneur avec la commande:

`docker build --no-cache -t {username Docker}/jguweka:OAS3 .`

Notons que pour la Q3, il faut lancer l'application Weka REST sur un conteneur en mappant le port 8849 du conteneur au port 8849 de l'hôte:

```docker run -p 8080:8080 -p 8849:8849 --link mongodb:mongodb {username Docker}/jguweka:OAS3```

Il faut ensuite se connecter en SSH au conteneur avec la commande:

`docker exec -it {Container Name} bash`

Dans le conteneur, effectuer les commande suivantes pour configurer JProfiler:

```
cd /usr/local/jrofiler9/
bin/jpenable
```

Télécharger JProfiler sur l'hôter et créer une nouvelle session pointant vers l'addresse IP du conteneur en spécifiant le port 8849. La commande `docker inspect {Container Name}` pour connaitre l'addresse IP.

