# Visual Basic 6 Plugin

[![Jenkins Plugins](https://img.shields.io/jenkins/plugin/v/visual-basic-6)](https://github.com/jenkinsci/visual-basic-6-plugin/releases)
[![Jenkins Plugin installs](https://img.shields.io/jenkins/plugin/i/visual-basic-6)](https://plugins.jenkins.io/visual-basic-6)
[![Build Status](https://ci.jenkins.io/buildStatus/icon?job=Plugins/visual-basic-6-plugin/master)](https://ci.jenkins.io/blue/organizations/jenkins/Plugins%2Fvisual-basic-6-plugin/branches)
[![MIT License](https://img.shields.io/github/license/jenkinsci/visual-basic-6-plugin.svg)](LICENSE)
[![javadoc](https://img.shields.io/badge/javadoc-available-brightgreen.svg)](https://javadoc.jenkins.io/plugin/visual-basic-6/)

[Jenkins](https://jenkins.io/) plugin for [Visual Basic 6](https://msdn.microsoft.com/en-us/library/hh127509.aspx) builder

Automating a build of a Visual Basic 6 has some challenges. This plugin aims to workaround some of these issues. 

# Usage
In Jenkins Configure System, section VB6 Builder, set the VB6.exe path. 

![ScreenShot](VB6_path.png?raw=true )

In a job configuration add a VB6 build step and define the path to the project file.  

![ScreenShot](job_config.png?raw=true)

In a [declarative pipeline](https://jenkins.io/doc/book/pipeline/syntax/#declarative-pipeline) use:
```
pipeline {
  agent any

  stages {
    stage('build') {
      steps {
        vb6 'Project1.vbp'
      }
    }
  }
}
```

## See also
http://zbz5.net/automating-build-visual-basic-6-project

## FAQ

Q: Does the plugin change the major, minor and the revision numbers in
vbp project before compile time?

A: Not at this time. Pull requests are welcome. 

## TODO Ideas

-   Add line numbers to project before compilation
