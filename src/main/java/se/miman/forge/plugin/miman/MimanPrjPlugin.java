package se.miman.forge.plugin.miman;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.events.PickupResource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.SetupCommand;

import se.miman.forge.plugin.miman.facet.MimanPrjFacet;

/**
 * Modifies a project to a MiMan project.
 * Also creates the parent project structure.
 */
@Alias("miman-prj")
@Help("A plugin that helps to build a MiMan project in an easy fashion")
@RequiresProject
public class MimanPrjPlugin implements Plugin
{
	@Inject
	private Event<InstallFacets> event;

	@Inject
	private Project project;

	@Inject
	private Event<PickupResource> pickup;

   @Inject
   private ShellPrompt prompt;
   
   @SetupCommand
   @Command(value = "setup", help = "Convert project to a Miman project")
   public void setup(PipeOut out) {

		if (!project.hasFacet(ResourceFacet.class)) {
	           event.fire(new InstallFacets(ResourceFacet.class));
		}
	   
		if (!project.hasFacet(MimanPrjFacet.class))
	           event.fire(new InstallFacets(MimanPrjFacet.class));
	       else
	           ShellMessages.info(out, "Project already an Miman project.");
   }
}
