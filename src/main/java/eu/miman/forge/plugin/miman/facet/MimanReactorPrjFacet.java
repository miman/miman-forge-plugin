/**
 * 
 */
package eu.miman.forge.plugin.miman.facet;

import java.util.Iterator;

import javax.inject.Inject;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Repository;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;

import eu.miman.forge.plugin.util.NazgulPrjUtil;
import eu.miman.forge.plugin.util.dto.ProjectWithPath;


/**
 *	Makes sure the reactor project has the 'miman-root' as a parent project and that the version is correct.  
 * @author Mikael Thorman
 */
@Alias("miman-reactor-facet")
@RequiresFacet({ MavenCoreFacet.class, JavaSourceFacet.class,
	DependencyFacet.class })
public class MimanReactorPrjFacet extends BaseFacet {

	@Inject
	ProjectFactory prjFactory;

   @Inject
   private ShellPrintWriter writer;
   
   private NazgulPrjUtil nazgulPrjUtil;

	public MimanReactorPrjFacet() {
		super();
		nazgulPrjUtil = new NazgulPrjUtil();
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.forge.project.Facet#install()
	 */
	@Override
	public boolean install() {
		installNazgulConfiguration();
		return true;
	}

	/* (non-Javadoc)
	 * @see org.jboss.forge.project.Facet#isInstalled()
	 */
	@Override
	public boolean isInstalled() {
		final MavenCoreFacet mvnFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mvnFacet.getPOM();

		// Change the parent to Nazgul project
		if (pom.getParent() == null) {
			return false;
		}
		ProjectWithPath parentPrj = nazgulPrjUtil.findParentProject(project, prjFactory);
		if (parentPrj != null && parentPrj.getProject().hasFacet(MavenCoreFacet.class)) {
			MavenCoreFacet parentMvnFacet = parentPrj.getProject().getFacet(MavenCoreFacet.class);
			Model parentPom = parentMvnFacet.getPOM();
			if (!parentPom.getGroupId().equals(pom.getParent().getGroupId())) {
				return false;
			}
			if (!parentPom.getArtifactId().equals(pom.getParent().getArtifactId())) {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	/**
	 * Set the project parent to MiMan project.
	 * Changes the project to a pom project.
	 */
	private void installNazgulConfiguration() {
		final MavenCoreFacet mvnFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mvnFacet.getPOM();

		// Change the parent to Nazgul project
		ProjectWithPath parentPrj = nazgulPrjUtil.findParentProject(project, prjFactory);
		MavenCoreFacet parentPrjMvnFacet = parentPrj.getProject().getFacet(MavenCoreFacet.class);
		Model parentPom = parentPrjMvnFacet.getPOM();
		
		if (parentPom.getParent() == null) {
			// The parent pom doesn't have any parent pom in its turn
			writer.println(ShellColor.RED, "Error - The parent pom.xml file doesn't have any parent!");
			writer.println("The parent pom is located here: " + parentPrj.getProject().getProjectRoot().getFullyQualifiedName());
			return;
		}
		
		if (pom.getParent() == null) {
			pom.setParent(new Parent());
		}
		pom.getParent().setGroupId(parentPom.getGroupId());
		pom.getParent().setArtifactId(parentPom.getArtifactId());
		pom.getParent().setVersion(parentPom.getVersion());
		pom.getParent().setRelativePath(parentPrj.getPathToParent() + "pom.xml");
		
		pom.setPackaging("pom");
		
		// The submodules should not have the Compiler plugin, it is a pom project
		Plugin compilerPlugin = pom.getBuild().getPluginsAsMap().get("org.apache.maven.plugins:maven-compiler-plugin");
		if (compilerPlugin != null) {
			pom.getBuild().getPlugins().remove(compilerPlugin);
		}
		
		// Remove the JBoss Nexus repo, it isn't used
		removeRepositoryWithId(pom, "JBOSS_NEXUS");
		
		mvnFacet.setPOM(pom);
	}

	private void removeRepositoryWithId(Model pom, String repoId) {
		Iterator<Repository> repos = pom.getRepositories().iterator();
		boolean jbossNexusFound = false;
		while (!jbossNexusFound && repos.hasNext()) {
			Repository repo = repos.next();
			if (repoId.equalsIgnoreCase(repo.getId())) {
				pom.getRepositories().remove(repo);
				jbossNexusFound = true;
			}
		}
	}
}
