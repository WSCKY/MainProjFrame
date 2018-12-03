package uwbRTLS.InstManager.Instance;

import uwbRTLS.uiComponent.uiAnchor;

public class uwbAnchor {
	private uwbInstance AnchorInst = null;
	private uiAnchor AnchorGUI = null;
	public uwbAnchor(uwbInstance inst, uiAnchor ui) {
		AnchorInst = inst;
		AnchorGUI = ui;
	}
	public uwbInstance getInst() {
		return AnchorInst;
	}
	public uiAnchor getUI() {
		return AnchorGUI;
	}
}
