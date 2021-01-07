import java.util.HashMap;
import java.util.Map;

public class MenuItem implements IMenuItem {

    private int relatedNumber;

    private String presentation;

    private OperationType operationType;

    private Class linkedClass;

    private String methodName;

    private Map<String, Object> parameters;

    public static MenuItem createAction(int relatedNumber, String presentation, OperationType operationType, Class linkedClass) {
        return new MenuItem(relatedNumber, presentation, operationType, linkedClass, null, null);
    }

    public static MenuItem createAction(int relatedNumber, String presentation, OperationType operationType,
                                              Class linkedClass, String methodName, Map<String, Object> parameters) {
        return new MenuItem(relatedNumber, presentation, operationType, linkedClass, methodName, parameters);
    }

    public static MenuItem createMenu(int relatedNumber, String presentation) {
        return new MenuItem(relatedNumber, presentation, OperationType.SHOW_MENU, null, null, null);
    }

    private MenuItem(int relatedNumber, String presentation, OperationType operationType,
                     Class linkedClass, String methodName, Map<String, Object> parameters) {
        this.relatedNumber = relatedNumber;
        this.presentation = presentation;
        this.operationType = operationType;
        this.linkedClass = linkedClass;
        if (methodName != null) this.methodName = methodName;
        else {
            if (this.operationType != null && this.linkedClass != null) {
                this.methodName = "process" + this.operationType.getAction() + this.linkedClass.getSimpleName();
            }
        }
        this.parameters = new HashMap<>();
        if (parameters != null) this.parameters.putAll(parameters);
    }

    private MenuItem(int relatedNumber, String presentation, OperationType operationType, Class linkedClass, String methodName) {
        this.relatedNumber = relatedNumber;
        this.presentation = presentation;
        this.operationType = operationType;
        this.linkedClass = linkedClass;
        this.methodName = methodName;
    }

    public int getRelatedNumber() {
        return relatedNumber;
    }

    public void setRelatedNumber(int relatedNumber) {
        this.relatedNumber = relatedNumber;
    }

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public Class getLinkedClass() {
        return linkedClass;
    }

    public void setLinkedClass(Class linkedClass) {
        this.linkedClass = linkedClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
