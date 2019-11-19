# Visual Basic 6 Plugin

[![Jenkins Plugins](https://img.shields.io/jenkins/plugin/v/visual-basic-6)](https://github.com/jenkinsci/visual-basic-6-plugin/releases)
[![Jenkins Plugin installs](https://img.shields.io/jenkins/plugin/i/visual-basic-6)](https://plugins.jenkins.io/visual-basic-6)


Jenkins plugin for Visual Basic 6 builder

Automating a build of a Visual Basic 6 has some challenges. This plugin aims to workaround some of these issues. 

# Usage
In Jenkins Configure System, section VB6 Builder, set the VB6.exe path. 

![ScreenShot](VB6_path.png?raw=true )

In a job configuration add a VB6 build step and define the path to the project file.  

![ScreenShot](job_config.png?raw=true)

## See also
http://zbz5.net/automating-build-visual-basic-6-project

## FAQ

Q: Does the plugin change the major, minor and the revision numbers in
vbp project before compile time?

A: Not at this time. Pull requests are welcome. 

## TODO Ideas

-   Add line numbers to project before compilation
