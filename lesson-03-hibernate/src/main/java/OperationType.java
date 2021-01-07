public enum OperationType {
    SHOW_MENU("Show menu", ""),
    FORM_REPORT("Form report", ""),
    BACK("Back", ""),
    QUIT("Quit", ""),
    DISCARD_CHANGES("Discard changes", ""),
    SAVE_CHANGES("Save changes", ""),
    FIND_ENTITY("Find %s", "Find"),
    ADD_ENTITY("Add %s", "Add"),
    EDIT_ENTITY("Edit %s", "Edit"),
    DELETE_ENTITY("Delete %s", "Delete");

    private final String pattern;
    private final String action;

    private OperationType(String pattern, String action) {
        this.pattern = pattern;
        this.action = action;
    }

    public String getPattern() {
        return pattern;
    }

    public String getAction() {
        return action;
    }

}
