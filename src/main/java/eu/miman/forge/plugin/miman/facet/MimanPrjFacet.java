/**
 * 
 */
package eu.miman.forge.plugin.miman.facet;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Repository;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;

import eu.miman.forge.plugin.util.VelocityUtil;

/**
 * 
 * @author Mikael Thorman
 */
@Alias("mimanfacet")
@RequiresFacet({ MavenCoreFacet.class, JavaSourceFacet.class,
		DependencyFacet.class })
public class MimanPrjFacet extends BaseFacet {

	public static final String PARENT_ARTIFACT_ID = "miman-root";
	public static final String PARENT_GROUP_ID = "se.miman.maven";
	public static final String MIMAN_ROOT_VERSION = "1.0.0";

	// private static final VelocityUtil VELOCITY_UTIL = new VelocityUtil();
	private static final String UTF_8 = "UTF-8";

	private final VelocityEngine velocityEngine;
	
	private VelocityUtil velocityUtil;

	public MimanPrjFacet() {
		super();
		velocityUtil = new VelocityUtil();
		
		velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER,
				"classpath");
		velocityEngine.setProperty("classpath.resource.loader.class",
				ClasspathResourceLoader.class.getName());
		velocityEngine.setProperty(
				RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
				"org.apache.velocity.runtime.log.JdkLogChute");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.forge.project.Facet#install()
	 */
	@Override
	public boolean install() {
		installNazgulConfiguration();
		createParentPomProjects();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.forge.project.Facet#isInstalled()
	 */
	@Override
	public boolean isInstalled() {
		final MavenCoreFacet mvnFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mvnFacet.getPOM();

		// Change the parent to MiMan project
		if (pom.getParent() == null) {
			return false;
		}
		if (!PARENT_GROUP_ID.equals(pom.getParent().getGroupId())) {
			return false;
		}
		if (!PARENT_ARTIFACT_ID.equals(pom.getParent().getArtifactId())) {
			return false;
		}
		return true;
	}

	// Helper functions ****************************************
	/**
	 * Set the project parent to Miman project. Changes the project to a pom
	 * project. Add the poms module
	 */
	private void installNazgulConfiguration() {
		final MavenCoreFacet mvnFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mvnFacet.getPOM();

		// Change the parent to Nazgul project
		if (pom.getParent() == null) {
			pom.setParent(new Parent());
		}
		pom.getParent().setGroupId(PARENT_GROUP_ID);
		pom.getParent().setArtifactId(PARENT_ARTIFACT_ID);
		pom.getParent().setVersion(MIMAN_ROOT_VERSION);

		pom.setPackaging("pom");

		if (pom.getModules() == null) {
			pom.setModules(new ArrayList<String>());
		}
		List<String> modules = pom.getModules();
		modules.add("poms");

		// The submodules should not have the Compiler plugin, it is a pom project
		Plugin compilerPlugin = pom.getBuild().getPluginsAsMap().get("org.apache.maven.plugins:maven-compiler-plugin");
		if (compilerPlugin != null) {
			pom.getBuild().getPlugins().remove(compilerPlugin);
		}
		
		// Remove the JBoss Nexus repo, it isn't used
		removeRepositoryWithId(pom, "JBOSS_NEXUS");
		
		mvnFacet.setPOM(pom);
	}

	/**
	 * This function creates: - the poms subfolder with its reactor pom - the 2
	 * parent projects under this folder with their pom files
	 */
	private void createParentPomProjects() {
		final MavenCoreFacet mvnFacet = project.getFacet(MavenCoreFacet.class);
		Model pom = mvnFacet.getPOM();

		DirectoryResource pomsDir = project.getProjectRoot()
				.getOrCreateChildDirectory("poms");

		String parentPrjName = pom.getArtifactId() + "-parent";
		pomsDir.getOrCreateChildDirectory(parentPrjName);
		createParentPomFile(pom, parentPrjName);

		// String modelParentPrjName = pom.getArtifactId() + "-model-parent";
		// pomsDir.getOrCreateChildDirectory(modelParentPrjName);
		// createModelParentPomFile(pom, modelParentPrjName);

		createPomsPomFile(pom, parentPrjName);
	}

	/**
	 * Creates the pom file for the parent project
	 * 
	 * @param pom
	 *            The parent pom
	 * @param parentPrjName
	 *            The name of the parent folder
	 */
	private void createParentPomFile(Model pom, String parentPrjName) {
		String parentPomUri = "/template-files/poms/parent/pom.xml";

		Map<String, Object> velocityPlaceholderMap = new HashMap<String, Object>();
		velocityPlaceholderMap.put("groupId", pom.getGroupId());
		velocityPlaceholderMap.put("artifactId", pom.getArtifactId());
		velocityPlaceholderMap.put("version", pom.getVersion());
		velocityPlaceholderMap.put("miman-version", MIMAN_ROOT_VERSION);

		String targetUri = "../../../poms/" + parentPrjName + "/pom.xml";
		VelocityContext velocityContext = velocityUtil
				.createVelocityContext(velocityPlaceholderMap);
		createResourceAbsolute(parentPomUri, velocityContext, targetUri);
	}

	// /**
	// * Creates the pom file for the parent project
	// * @param pom The parent pom
	// * @param parentPrjName The name of the parent folder
	// */
	// private void createModelParentPomFile(Model pom, String parentPrjName) {
	// String parentPomUri = "/template-files/model-parent/pom.xml";
	//
	// Map<String, Object> velocityPlaceholderMap = new HashMap<String,
	// Object>();
	// velocityPlaceholderMap.put("groupId", pom.getGroupId());
	// velocityPlaceholderMap.put("artifactId", pom.getArtifactId());
	// velocityPlaceholderMap.put("version", pom.getVersion());
	//
	// String targetUri = "../../../poms/" + parentPrjName + "/pom.xml";
	// VelocityContext velocityContext =
	// VelocityUtil.createVelocityContext(velocityPlaceholderMap);
	// createResourceAbsolute(parentPomUri, velocityContext, targetUri);
	// }

	/**
	 * Creates the pom file for the parent project
	 * 
	 * @param pom
	 *            The parent pom
	 * @param parentPrjName
	 *            The name of the parent folder
	 */
	private void createPomsPomFile(Model pom, String parentPrjName) {
		String parentPomUri = "/template-files/poms/pom.xml";

		Map<String, Object> velocityPlaceholderMap = new HashMap<String, Object>();
		velocityPlaceholderMap.put("groupId", pom.getGroupId());
		velocityPlaceholderMap.put("artifactId", pom.getArtifactId());
		velocityPlaceholderMap.put("version", pom.getVersion());
		velocityPlaceholderMap.put("miman-version", MIMAN_ROOT_VERSION);
		velocityPlaceholderMap.put("parent-prj-name", parentPrjName);

		String targetUri = "../../../poms/pom.xml";
		VelocityContext velocityContext = velocityUtil
				.createVelocityContext(velocityPlaceholderMap);
		createResourceAbsolute(parentPomUri, velocityContext, targetUri);
	}

	/**
	 * Stores the template file as the target file after running a velocity
	 * merge on the template file.
	 * 
	 * @param templateFilePath
	 *            The template file to store after replacing all velocity
	 *            placeholders
	 * @param velocityContext
	 *            The velocity placeholder mappings
	 * @param targetFilePath
	 *            The target file path + name
	 * @return The target file
	 */
	public FileResource<?> createResourceAbsolute(String templateFilePath,
			VelocityContext velocityContext, String targetFilePath) {
		ResourceFacet resources = project.getFacet(ResourceFacet.class);

		StringWriter stringWriter = new StringWriter();
		velocityEngine.mergeTemplate(templateFilePath, UTF_8, velocityContext,
				stringWriter);

		System.out.println("Storing file in: " + targetFilePath);
		FileResource<?> createdResource = resources.createResource(stringWriter
				.toString().toCharArray(), targetFilePath);
		return createdResource;
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
