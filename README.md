# THIS README MAY CAUSING ((MAKE && (YOU ANNOY || 429)))
[![](https://github.com/TaYaKi71751/HttpTest/actions/workflows/main.yml/badge.svg)](https://github.com/TaYaKi71751/HttpTest/actions/workflows/main.yml)
[![License: WTFPL](https://img.shields.io/badge/License-WTFPL-brightgreen.svg)](http://www.wtfpl.net/about/)
## .sh & .har
```
//TODO 
java.io.File shellFile;
new Firefox(popcat_url).asSecretMode(true).withDeveloperMenu(true).execute().then(this -> {
  this.pop(cat).then((void) ->{
    Tab* networkTab = this->developerMenu->networkTab;
    networkTab->saveAllAsHar(new java.io.File($SAME_DIRECTORY_WHERE_JAR_FILE).getPath() 
         + "/" + ($HAR_FILE_NAME.contains(" ") ? $HAR_FILE_NAME.replace(" ",""):$HAR_FILE_NAME) + ".har");
    char* reloadCurl = networkTab->search("reload").first().copyAsCurl().clipboardString.toCharArray();
    if(!(shellFile = new java.io.File(new java.io.File($SAME_DIRECTORY_WHERE_JAR_FILE).getPath() + "/"
      + $SHELL_FILE_NAME + ".sh")).canWrite()){
        new super->Shell(){{
            changeDirectory($SAME_DIRECTORY_WHERE_JAR_FILE);  
            execute("touch " + $SHELL_FILE_NAME + ".sh");
            close();
        }}.waitFor();
    }
    if(shellFile.canWrite()){
        new FileWriter(shellFile.getPath(), false){{
            for(char c : reloadCurl){
                write(c);
            }
            close();
        }}.waitFor();
    }
  }); 
 });
 shellFile = NULL;
 System.gc();
```
