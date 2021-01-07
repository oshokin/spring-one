import java.util.ArrayList;
import java.util.List;

//в 1С похожая структура данных называется ValueTree;
//в Java почему-то не нашел ее реализаций (по сути, бинарного дерева)
public class TreeNode<T> {

    private TreeNode<T> parent = null;

    private T data = null;

    private List<TreeNode<T>> children = new ArrayList<>();

    public TreeNode(T data) {
        this.data = data;
    }

    private void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public TreeNode<T> addChild(TreeNode<T> child) {
        child.setParent(this);
        this.children.add(child);
        return child;
    }

    public void addChildren(List<TreeNode<T>> children) {
        children.forEach(each -> each.setParent(this));
        this.children.addAll(children);
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public void deleteNode() {
        if (parent != null) {
            this.parent.getChildren().remove(this);
            for (TreeNode<T> node : getChildren()) {
                node.deleteNode();
            }
        }
        this.getChildren().clear();
    }

}