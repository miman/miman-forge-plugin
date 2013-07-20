package eu.miman.forge.plugin.miman.completer;


public enum ParentTypeType {
	   NORMAL("normal", "Normal project"),
	   DATABASE("db", "Database access project"),
	   API("api", "API project, containing interfaces & interface models");

	   private String type;
	   private String description;

	   private ParentTypeType(final String type, final String description)
	   {
	      setType(type);
	      setDescription(description);
	   }

	   public String getType()
	   {
	      return type;
	   }

	   private void setType(String type)
	   {
	      if (type != null)
	      {
	         type = type.trim().toLowerCase();
	      }
	      this.type = type;
	   }

	   @Override
	   public String toString()
	   {
	      return type;
	   }

	   public String getDescription()
	   {
	      return description;
	   }

	   private void setDescription(final String description)
	   {
	      this.description = description;
	   }

	   public static ParentTypeType from(String type)
	   {
		   ParentTypeType result = NORMAL;
	      if ((type != null) && !type.trim().isEmpty())
	      {
	         type = type.trim();
	         for (ParentTypeType p : values())
	         {
	            if (p.getType().equals(type) || p.name().equalsIgnoreCase(type))
	            {
	               result = p;
	               break;
	            }
	         }
	      }
	      return result;
	   }
}
