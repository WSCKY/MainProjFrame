package uwbRTLS.InstManager;

import java.util.EventListener;

public interface AnchorManagerEventListener extends EventListener {
	public void AnchorUpdated(AnchorManagerEvent event);
}
