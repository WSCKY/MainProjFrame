package protocol.event;

import java.util.EventListener;

public interface DecodeEventListener extends EventListener {
	public void getNewPackage(DecodeEvent event);
	public void badCRCEvent(DecodeEvent event);
}
