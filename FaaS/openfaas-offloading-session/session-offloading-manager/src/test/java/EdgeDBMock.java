import com.openfaas.function.api.IEdgeDB;
import com.openfaas.model.IRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EdgeDBMock implements IEdgeDB {

    HashMap<String, String> values;
    HashMap<String, List<String>> lists;

    String sessionId;

    public EdgeDBMock(IRequest req) {
        sessionId = req.getHeader("X-session");

        System.out.println("(EdgeDB.Constructor) X-session: <" + sessionId + ">");

        values = new HashMap<>();
        lists = new HashMap<>();
    }

    public EdgeDBMock(String session) {
        sessionId = session;
        System.out.println("(EdgeDB.Constructor) Not-offloadable session: <" + sessionId + ">");

        values = new HashMap<>();
        lists = new HashMap<>();
    }

    @Override
    public void close() {

    }

    @Override
    public String get(String key) {
        if (!values.containsKey(key))
            return null;
        return values.get(key);
    }

    @Override
    public void set(String key, String value) {
        values.put(key, value);
    }

    @Override
    public List<String> getList(String key) {
        if (!lists.containsKey(key))
            return null;
        return lists.get(key);
    }

    @Override
    public void addToList(String key, String value) {
        if (!lists.containsKey(key)) {
            lists.put(key, new ArrayList<>());
        }
        lists.get(key).add(value);
    }

    @Override
    public void propagate(String value, String levelToPropagateTo, String function) {
        System.out.println("Propagate call: \n\tvalue: " + value + "\n\tlevelToPropagateTo: " + levelToPropagateTo + "\n\t" + "function: " + function + "\n\n");
    }

    @Override
    public void setTTL(long seconds) {

    }

    @Override
    public void delete(String key) {
        if (values.containsKey(key)) {
            values.remove(key);
        } else {
            lists.remove(key);
        }
    }
}
