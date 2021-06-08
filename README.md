# Bundle Installer
A Minecraft client modpack installer for Fabric mods, supporting the vanilla launcher. <br/>
<br/>
**TO DO:**
- [✅] Read from a configuration file
- [✅] Download mods from the configuration file
- [✅] Allow the configuration file to be hosted remotely
- [✅] Download mods to the Minecraft game directory
- [✅] Have a GUI
- [✅] GUI shows all download configurations
- [✅] GUI lets you choose the game directory to download to
- [❌] GUI allows you to select and deselect mods in a configuration
- [⛔] Create Bundle Loader, a fork of Fabric Loader that reads only mods and configs from a separate game directory while using vanilla's directory for everything else
- [⛔] Install Bundle Loader or Fabric Loader as necessary
- [✅] Install Quilt Loader and create a game profile/version
- [❌] Merge of https://github.com/QuiltMC/quilt-loader/pull/20 into Quilt Loader, allowing Bundle to change the mods and configs folder to read from for non-separate-game-dir installations
- [❌] Allow for more customization of the installer GUI (logo, layout, etc)

## What it can do now
Bundle Installer currently only works as a basic mod downloader. It can download a collection of mods, each being either from [CurseForge](https://www.curseforge.com/) or from a direct file link. In the installer UI, you can choose the game directory (`.minecraft` folder) to "install" to, and in that directory it will create further directories: `.bundle/<installation>/mods` in which mods will be downloaded to. You can customize the list of mods to download in the installer's `installer_config.json` file, before compiling the installer. You can customize some aspects of the UI in `installer.properties`. <br/>
<br/>
To make a Bundle pack, it is recommended to clone this repository or fork the project. <br/>
<br/>
The first file you can change will be the `installer.properties` file, located in `app/src/main/resources/`. Here you may set the width, height, and resizability of the installer window. <br/>
<br/>
**Example `installer.properties`**
```properties
width=500
height=300
resizable=false

launcher_icon=Dirt
launcher_profile_id=Example Modpack
```
<br/>

The next file to change will be `installer_config.json`, also located in `app/src/main/resources/`. This file will contain your download configurations, or installations the user will be able to choose from in the installer UI. <br/>
<br/>
**Example `installer_config.json`**
```json
{
    "download_configs": {
        "Example Download Config": {
            "id": "example-0.0.0+1.16.5",
            "loader_version": "0.13.1-rc.6",
            "game_version": "1.16.5",
            "separate_game_dir": true,
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
        },
        "Example Download Config 2": {
            "id": "example-two-0.0.0+1.16.5",
            "loader_version": "0.13.1-rc.6",
            "game_version": "1.16.5",
            "separate_game_dir": true,
            "downloads": {
                "Example Project": [
                    {
                        "type": "direct",
                        "data": {
                            "url": "https://example.com/example-2.jar"
                        } 
                    }
                ]
            }
        }
    }
}
```
- `"include": [strings ... ]` _OPTIONAL_ - A string array containing direct urls to .json files to be used as installer configs. This can be used to remove the need to download new installers for each version of the pack, and instead update the remotely hosted json file. Remote json files cannot contain this field in order to prevent daisy-chained or recursive inclusion, and it will be ignored in those remote files.
- `"download_configs" : {}` Contains all your download configs.
    - `"<download config name>" : {}` A download config, containing its id and downloads. Can be multiple.
       - `"id" : string` A unique installation id for your download config. This will be the game version id, and should preferably include the version of this download config as well as the target Minecraft version. <br/>
       - `"loader_version" : string` The valid string for the version of Quilt Loader your download config will use, for example `0.13.1-rc.6`. You can find all versions listed [here.](https://meta.quiltmc.org/v3/versions/loader) <br/>
       - `"game_version" : string` The valid string for the version of Minecraft your download config will use, for example `1.16.5`. <br/>
       - `"separate_game_dir" : boolean` Whether to use an entirely different game directory (for content modpacks), or use the vanilla directory while accessing only mods and configs separately (**This will only work with a Quilt loader version merging [this PR's](https://github.com/QuiltMC/quilt-loader/pull/20) code**) <br/>
       - `"copy_game_dir" : string` _OPTIONAL_ - The name of a zip file (with `.zip` extension) located in `src/main/resources/` containing files you wish to copy into the bundle game directory. (Useful for including configuration files) <br/>
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
- Modrinth - `"modrinth"`, Data Object Contains:
    - `"version_id" : string` - The version's ID string in the URL to the version.
    - `"sha1" : string` - The sha1 hash of the desired file in the version (viewable with Modrinth's api)
<br/><br/>
- Direct URL - `"direct"`, Data Object Contains:
    - `"url" : string` - The URL to a direct download for the mod jar file.
    
    
## Building your Installer Jar

Once you have configured these files, you can build this project into a working installer jar file. <br/>
<br/>
In order to do so, open a terminal and navigate to the root of the project. Run the command `gradlew shadowJar` or `./gradlew shadowJar` (PowerShell) and the jar will be built. <br/>
<br/>
You will find the built installer jar in `app/build/libs`. Running the jar will open the UI and allow you to download the mods.
                

 