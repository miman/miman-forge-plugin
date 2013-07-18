miman-forge-plugin
==================

Forge plugins to handle miman project structure projects

This is a plugin for creating a root project structure similar to the Nazgul project but with a minimal root pom without all rigorous rules.


Introduction
------------
The Miman JBoss Forge plugin is used to get a good structure on your project if it is to contain sub maven projects.

This plugin should only be used on the root project.

By building the project in this way you can have different root projects for different types of projects (model, web, api...) without that being part of the reactor pom structure.

Details
-------
The Miman JBoss Forge plugin modifies a project in the following way:

Adds a parent to the maven pom (miman-root)
Adds a poms reactor sub-project
Adds the parent project that should be used as a parent for all maven-projects within the project. (this is placed in the poms directory)
Plugin commands
This chapter describes all commands for this plugin

### setup
This command modifies the project to be a miman root project.

It modifies the project in the following way:

Adds a parent to the maven pom (miman-root)
Adds a poms reactor sub-project
Adds the parent project that should be used as a parent for all maven-projects within the project. (this is placed in the poms directory)
