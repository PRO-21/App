# PDFAuthenticator

Pour utiliser l'application, il vous suffit de télécharger une des 3 versions dans l'onglet *Releases* selon votre système d'exploitation.



## Version Windows

Prérequis : [Java JDK 11](https://adoptopenjdk.net/)

Une fois la release téléchargée et le JDK installé : 

- Placez le fichier *PDFAuthenticator_win.jar* à l'endroit de votre choix 
- Ouvrez un terminal dans le dossier où vous l'avez placé
- Tapez la commande `java -jar PDFAuthenticator_win.jar` pour lancer l'application.



## Version Linux

Prérequis : Java JDK 14 à installer avec la commande `sudo apt install openjdk-14-jre-headless`

Une fois la release téléchargée et le JDK installé : 

- Placez le fichier *PDFAuthenticator_linux.jar* à l'endroit de votre choix 
- Ouvrez un terminal dans le dossier où vous l'avez placé
- Tapez la commande `java -jar PDFAuthenticator_linux.jar` pour lancer l'application.



## Version MacOS

Prérequis : [Java JDK 16](https://adoptopenjdk.net/releases.html?variant=openjdk16&jvmVariant=hotspot)

Une fois la release téléchargée et le JDK installé : 

- Placez le fichier *PDFAuthenticator_mac.jar* à l'endroit de votre choix 
- Ouvrez un terminal dans le dossier où vous l'avez placé
- Tapez la commande `java -jar PDFAuthenticator_mac.jar` pour lancer l'application.



## Attention

**Assurez-vous de ne pas avoir un fichier nommé *token* dans le dossier où vous placez le fichier .jar**. Car en se connectant avec vos identifiants dans l'application, il risque d'être remplacé par celui créé lors de l'authentification.



Le message suivant lors du lancement de l'application n'empêche pas l'application de fonctionner correctement :

```
mai 20, 2021 9:04:27 PM com.sun.javafx.application.PlatformImpl startup
AVERTISSEMENT: Unsupported JavaFX configuration: classes were loaded from 'unnamed module @161fae78'
```



Pour savoir si vous avez déjà un JDK installé, il faut ouvrir un terminal et vérifier si la commande `java --version` vous retourne bien un numéro de version et sinon vous devez installer un JDK en fonction de ce qui est indiqué plus haut.
