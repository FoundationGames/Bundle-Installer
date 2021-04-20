# Bundle Installer
A Minecraft client modpack installer for Fabric mods, supporting the vanilla launcher. <br/>
<br/>
**TO DO:**
- [✅] Read from a configuration file
- [✅] Download mods from the configuration file
- [✅] Allow the configuration file to be hosted remotely
- [❌] Download mods to the Minecraft game directory
- [❌] Have a GUI
- [❌] GUI shows all download configurations
- [❌] GUI lets you choose the game directory to download to
- [❌] GUI allows you to select and deselect mods in a configuration
- [❌] Create Bundle Loader, a fork of Fabric Loader that reads only mods and configs from a separate game directory while using vanilla's directory for everything else
- [❌] Install Bundle Loader or Fabric Loader as necessary
- [❌] Allow for more customization of the installer GUI (logo, layout, etc)

## What it can do now
Bundle Installer currently only works as a basic mod downloader. It can download a collection of mods, each being either from [CurseForge](https://www.curseforge.com/) or from a direct file link. You can customize the list of mods to download in the installer's `installer_config.json` file, before compiling the installer. The installer has no user interface at the moment, and will only download mods to the directory set in the installer's `installer.properties` file. <br/>
<br/>
To make a Bundle pack, it is recommended to clone this repository or fork the project. <br/>
<br/>
The first file to change will be the `installer.properties` file, located in `app/src/main/resources/`. The only working fields are `dev_download_dir` and `dev_download_config`. The download dir must be set to a valid directory on your device, and the download config must be set to the name of a valid download config specified in the installer config json. When run, all mods from the specified download config will be downloaded to the specified directory. <br/>
<br/>
**Example `installer.properties`**
```properties
dev_download_dir=C:/path/to/download/folder/
dev_download_config=Example Download Config
```
<br/>

The next file to change will be `installer_config.json`, also located in `app/src/main/resources/`. This file will contain your download configurations, or installations the user will be able to choose from. Choosing the download config is not implemented yet as there is no UI, so it will choose the one specified in the `installer.properties`. <br/>
<br/>
**Example `installer_config.json`**
```json
{
    "download_configs": {
        "Example Download Config": {
            "id": "example",
            "downloads": {
                "Example Curse Project": [
                    {
                        "type": "curse",
                        "data": {
                            "project": 0,
                            "file": 0
                        }
                    }
                ],
                "Example Direct Download": [
                    {
                        "type": "direct",
                        "data": {
                            "url": "https://example.com/example.jar"
                        }
                    }
                ],
                "Example Multi-Download": [
                    {
                        "type": "curse",
                        "data": {
                            "project": 0,
                            "file": 0
                        }
                    },
                    {
                        "type": "direct",
                        "data": {
                            "url": "https://example.com/example.jar"
                        }
                    }
                ]
            }
        }
    }
}
```
- `"include": [strings ... ]` _OPTIONAL_ - A string array containing direct urls to json files, formatted like this one, to be used as installer configs. This can be used to remove the need to download new installers for each version of the pack, and instead update the remotely hosted json file. Remote json files cannot contain this field and it will be ignored.
- `"download_configs" : {}` Contains all your download configs.
    - `"<download config name>" : {}` A download config, containing its id and downloads. Can be multiple.
       - `"id" : string` A unique installation id for your download config. Unused for now, but it's important that this be a unique identifier which is a valid file name. <br/>
       - `"downloads" : {}` Contains this download config's downloads. <br/>
           - `"<download name>" : []` A download object array, containing all the possible download sources in order of priority. Only one of the downloads in the array will actually be used, and the downloads will be ordered in priority (i.e. the first one will be tested to work first, and so on). This is in case a mod is removed from one source online, it is possible to have backups.
               - `{}` A download object, with a type and a data object.
                   - `"type" : string` The download type. Can be any of the following **Download Types** listed on this page below.
                   - `"data" : {}` The download type's data. Must contain the correct fields for the object's type.
                   
#### Download Types
- CurseForge - `"curse"`, Data Object Contains:
    - `"project" : int` - The Project ID found on the CurseForge page.
    - `"file" : int` - The File ID found at the end of the file page's URL.
<br/><br/>
- Direct URL - `"direct"`, Data Object Contains:
    - `"url" : string` - The URL to a direct download for the mod jar file.
    
    
## Building your Installer Jar

Once you have configured these files, you can build this project into a working installer jar file. <br/>
<br/>
In order to do so, open a terminal and navigate to the root of the project. Run the command `gradlew shadowJar` or `./gradlew shadowJar` (PowerShell) and the jar will be built. <br/>
<br/>
You will find the built installer jar in `app/build/libs`. Running the jar will download the mods.
                

 