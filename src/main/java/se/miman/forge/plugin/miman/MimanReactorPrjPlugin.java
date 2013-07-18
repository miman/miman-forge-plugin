package se.miman.forge.plugin.miman;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.SetupCommand;

import se.miman.forge.plugin.miman.facet.MimanReactorPrjFacet;

/**
 * Modifies a project to a Miman reactor project (has the 'miman-root' as a parent in the pom).
 */
@Alias("miman-reactor-prj")
@Help("A plugin that helps to build a Miman reactor project")
@RequiresProject
public class MimanReactorPrjPlugin implements Plugin
{
	@Inject
	private Event<InstallFacets> event;

	@Inject
	private Project project;

   @SetupCommand
   @Command(value = "setup", help = "Convert project to a Miman reactor project")
   public void setup(PipeOut out) {

		if (!project.hasFacet(ResourceFacet.class)) {
	           event.fire(new InstallFacets(ResourceFacet.class));
		}
	   
		if (!project.hasFacet(MimanReactorPrjFacet.class))
	           event.fire(new InstallFacets(MimanReactorPrjFacet.class));
	       else
	           ShellMessages.info(out, "Project already an Miman reactor project.");
   }
}
