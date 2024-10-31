package event.pipeline;

class Variable {
    private String name;
    private String value;
    private String processInstanceKey;

    // Getter und Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getprocessInstanceKey() {
        return processInstanceKey;
    }

    public void setprocesInstanceKey(String processInstanceKey) {
        this.processInstanceKey = processInstanceKey;
    }
    
}
