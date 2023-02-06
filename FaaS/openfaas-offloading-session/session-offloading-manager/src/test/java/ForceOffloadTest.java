import com.openfaas.function.Handler;
import com.openfaas.model.IHandler;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ForceOffloadTest {

    @Test
    public void handlerIsNotNull() {
        IHandler handler = new Handler();
        assertNotNull("Expected handler not to be null", handler);
    }
}
