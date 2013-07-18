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

import se.miman.forge.plugin.miman.facet.MimanModuleFacet;

/**
 * Modifies a project to a MiMan module, which means pointing to the correct parent project.
 */
@Alias("miman-module")
@Help("A plugin that converts a project to a miman module")
@RequiresProject
public class MimanModulePlugin implements Plugin
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
   @Command(value = "setup", help = "Convert project to a Miman module")
   public void setup(
//		   @Option(name = "parentType", completer=ParentTypeCompleter.class, required=true) String parentType,
		   PipeOut out) {

		if (!project.hasFacet(ResourceFacet.class)) {
	           event.fire(new InstallFacets(ResourceFacet.class));
		}
	   
		if (!project.hasFacet(MimanModuleFacet.class)) {
//			if (parentType == null) {
//				MimanModuleFacet.setParentType(ParentTypeType.NORMAL);
//			} else if (parentType.compareToIgnoreCase(ParentTypeType.NORMAL.getType()) == 0) {
//				MimanModuleFacet.setParentType(ParentTypeType.NORMAL);
//			} else if (parentType.compareToIgnoreCase(ParentTypeType.DATABASE.getType()) == 0) {
//				MimanModuleFacet.setParentType(ParentTypeType.DATABASE);
//			} else if (parentType.compareToIgnoreCase(ParentTypeType.API.getType()) == 0) {
//				MimanModuleFacet.setParentType(ParentTypeType.API);
//			} else {
//				ShellMessages.info(out, "Invalid parent type entered !");
//				return;
//			}
           event.fire(new InstallFacets(MimanModuleFacet.class));
		}
	       else
	           ShellMessages.info(out, "Project already an Miman module.");
   }
}
