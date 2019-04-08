# cdss-app-statemodgui-java #

This repository contains the StateMod GUI main application source code and supporting files for the development environment.
Several other repositories are used to create the StateMod GUI application.
Eclipse is used for development and repositories currently contain Eclipse project files to facilitate
setting up the Eclipse development environment.

StateMod GUI is part of
[Colorado's Decision Support Systems (CDSS)](https://www.colorado.gov/cdss) and provides a Graphical User Interface for
the State of Colorado's StateMod model.
See the following online resources:

* [Colorado's Decision Support Systems](https://www.colorado.gov/cdss)
* [OpenCDSS](http://learn.openwaterfoundation.org/cdss-emod-dev/) - currently
hosted on the Open Water Foundation website while the OpenCDSS server is configured

This README serves as the developer documentation.
See TSTool developer documentation for more expansive information for similar Eclipse/Java development environment.

* [Repository Status](#repository-status)
* [StateMod GUI and StateMod Integration](#statemod-gui-and-statemod-integration)
* [Repository Folder Structure](#repository-folder-structure)
* [Repository Dependencies](#repository-dependencies)
* [Development Environment Folder Structure](#development-environment-folder-structure)
* [Configuring Eclipse Workspace](#configuring-eclipse-workspace)
* [Compiling](#compiling)
* [Contributing](#contributing)
* [License](#license)
* [Contact](#contact)

-----

## Repository Status ##

See the next section for background on the repository contents.
The most recent code has been moved into GitHub.
However, given that the StateMod GUI is not actively developed,
some of the automated processes used to release the software have not been fully tested.
If a release needs to be made, the build processes will need to be fully tested.
These processes are similar to TSTool.
The software can be run from the development environment.

## StateMod GUI and StateMod Integration ##

The code in this repository is an archive of the latest StateMod GUI code.
The StateMod software is written in Fortran whereas the StateMod GUI is written in Java.
This means that integration of the software occurs via command-line when running StateMod from the GUI,
and via input and output files.

The overall workflow for "serious" modelers has always been to use software such as TSTool and StateDMI
to automate creating StateMod input files and to process output.
The StateMod GUI was designed to view StateMod input, run StateMod, and view output.
However, features to edit input files resulted in inconsistencies with the automated processes.
This design issue would need to be resolved to make the GUI more useful.

The StateMod GUI also includes features for a map interface,
However, model datasets often were not packaged with spatial data files, thus limiting the use of the GUI's map features.
Implementing an updated GUI would need to address this issue.

## Repository Folder Structure ##

The following are the main folders and files in this repository, listed alphabetically.
See also the [Development Environment Folder Structure](#development-environment-folder-structure)
for overall folder structure recommendations.

```
cdss-app-statemodgui-java/    StateMod GUI source code and development working files.
  .classpath                  Eclipse configuration file.
  .git/                       Git repository folder (DO NOT MODIFY THIS except with Git tools).
  .gitattributes              Git configuration file for repository.
  .gitignore                  Git configuration file for repository.
  .project                    Eclipse configuration file.
  bin/                        Eclipse folder for compiled files (dynamic so ignored from repo).
  build-util/                 Utility scripts used in development environment.
  conf/                       Configuration files for installer build tools.
  dist/                       Folder used to build distributable installer (ignored from repo).
  doc/                        Word/PDF documentation.
  externals/                  Third-party libraries and tools (may remove/move in future).
  installer/                  Files used to create installer.
  LICENSE.md                  StateMod GUI license file.
  README.md                   This file.
  resources/                  Additional resources, such as runtime files for installer.
  scripts/                    Eclipse run and external tools configurations.
  src/                        Main application source code.
  test/                       Test configurations.
```

## Repository Dependencies ##

Repository dependencies fall into three categories as indicated below.

### StateMod GUI Repository Dependencies ###

The main StateMod GUI code depends on other repositories
The following repositories are used to create the main StateMod GUI application.
Some repositories correspond to Eclipse projects and others are not used within Eclipse,
indicated as follows:

* Y - repository is included as Eclipse project.
* Y2 - repository is currently included as Eclipse project but may be phased out or
converted to a plugin because code is obsolete or is specific to third parties.
* y - repository is included as Eclipse project but does not need to be.  The project may have been added to Eclipse to use the Git client,
but files are often edited external to Eclipse.
* N - repository is managed outside if Eclipse,
such as documentation managed with command line Git or other Git tools.

|**Repository**|**Eclipse project?**|**Description**|
|-------------------------------------------------------------------------------------------------------------|--|----------------------------------------------------|
|`cdss-app-statemodapp-java`                                                                       |Y |StateMod GUI main application code (this repo).|
|[`cdss-archive-nsis-2.46`](https://github.com/OpenCDSS/cdss-archive-nsis-2.46)                    |N |Archive of NSIS 2.46, to set up development environment.|
|[`cdss-lib-cdss-java`](https://github.com/OpenCDSS/cdss-lib-cdss-java)                            |Y |Library that is shared between CDSS components.|
|[`cdss-lib-common-java`](https://github.com/OpenCDSS/cdss-lib-common-java)                        |Y |Library of core utility code used by multiple repos.|
|[`cdss-lib-cdss-java`](https://github.com/OpenCDSS/cdss-lib-cdss-java)                            |Y |Library that is shared between CDSS components.|
|[`cdss-util-buildtools`](https://github.com/OpenCDSS/cdss-util-buildtools)                        |Y |Tools to create CDSS Java software installers.|

### Repositories that Depend on StateMod GUI Repository ###

This repository is not known to be a dependency for any other projects.

## Development Environment Folder Structure ##

The following folder structure is recommended for StateMod GUI development.
Top-level folders should be created as necessary.
Repositories are expected to be on the same folder level to allow cross-referencing
scripts in those repositories to work.

```
C:\Users\user\                               Windows user home folder (typical development environment).
/home/user/                                  Linux user home folder (not tested).
/cygdrive/C/Users/user                       Cygdrive home folder (not tested).
  cdss-dev/                                  Projects that are part of Colorado's Decision Support Systems.
    StateModGUI/                             StateMod GUI product folder.
      eclipse-workspace/                     Folder for Eclipse workspace, which references Git repository folders.
                                             The workspace folder is not maintained in Git.
      git-repos/                             Git repositories for TSTool.
        cdss-app-statemodgui-java/
        cdss-archive-nsis-2.46/
        cdss-lib-cdss-java/
        cdss-lib-common-java/
        cdss-lib-models-java/
        cdss-util-buildtools/
```

## Configuring Eclipse Workspace ##

1. The Eclipse software should be installed as per TSTool development environment.
2. In the `git-repos` folder, clone the main repository with:  `git clone https://github.com/OpenCDSS/cdss-app-statemodgui-java.git`.
3. For the resulting repository, use Git Bash to run `build-util/git-clone-all-smgui.sh`,
which will clone all other needed repositories (will skip any that are already cloned).
4. Create the `eclipse-workspace` folder as shown in the above folder structure.
5. Start Eclipse, for example by running the `build-util/run-eclipse-win32.bat` batch file in a command prompt window.
Open the workspace folder created above, which will create the `.metadata` folder.
6. In Eclipse, use the ***File / Import / General / Existing Projects into Eclipse***.
Browse to the `git-repos` folder and select all the projects that are listed and click on ***Finish***.
7. The projects should import and compile automatically.
8. The `build-util` git scripts are useful for checking the status on all repositories and performing common tasks.

## Compiling, Running, and Distributing ##

The software should compile automatically when the Eclipse workspace is opened.
Run using the ***Run Configurations***, for example ***StateModGUI*** run configuration to launch the StateMod GUI.

To distribute the software, run the ***External Tool Configurations*** in Eclipse:

1. For the legacy software, which uses PDF documenation in the installer, first need to build the PDF docuementation
by merging separate PDF files into one large document as `doc/UserManual/dist/StateModGUI.pdf`.
Each PDF file should be an even number of pages.  **The tool to do the merge needs to be udpated (OWF developed a custom tool)**.
2. Run the ***Clean and build local install, no setup.exe (requires merged PDF)*** external tool.
This creates a local folder structure in `dist`.
3. Run the ***Local Install to Setup.exe (requires local install)*** external tool.
This will create a self-extracting installer in `dist`, suitable for distribution.

## Contributing ##

Contributions to this project can be submitted using the following options:

1. StateMod GUI software developers with commit privileges can write to this repository
as per normal OpenCDSS development protocols.
2. Post an issue on GitHub with suggested change.  Provide information using the issue template.
3. Fork the repository, make changes, and do a pull request.
Contents of the current master branch should be merged with the fork to minimize
code review before committing the pull request.

See also the [OpenCDSS / StateMod GUI protocols](http://learn.openwaterfoundation.org/cdss-website-opencdss/statemodgui/statemodgui/).

## License ##

Copyright Colorado Department of Natural Resources.

The software is licensed under GPL v3+. See the [LICENSE.md](LICENSE.md) file.

## Contact ##

See the [OpenCDSS StateMod GUI information for product contacts](http://learn.openwaterfoundation.org/cdss-website-opencdss/statemodgui/statemodgui/#product-leadership).
