import java.util.Map;

public interface IMenuItem {
    int getRelatedNumber();
    String getPresentation();
    OperationType getOperationType();
    String getMethodName();
    Map<String, Object> getParameters();
    void setParameters(Map<String, Object> parameters);
}
