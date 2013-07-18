/**
 * 
 */
package se.miman.forge.plugin.miman.completer;

import java.util.Arrays;

import org.jboss.forge.shell.completer.SimpleTokenCompleter;

/**
 * @author Mikael
 *
 */
public class ParentTypeCompleter extends SimpleTokenCompleter {

	/* (non-Javadoc)
	 * @see org.jboss.forge.shell.completer.SimpleTokenCompleter#getCompletionTokens()
	 */
	@Override
	public Iterable<?> getCompletionTokens() {
		return Arrays.asList(ParentTypeType.NORMAL, ParentTypeType.DATABASE, ParentTypeType.API);
	}

}
