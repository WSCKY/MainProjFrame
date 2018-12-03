package uwbRTLS.InstManager.Instance;

import uwbRTLS.signComponent.signAnchor;

public class uwbAnchor {
	private uwbInstance AnchorInst = null;
	private signAnchor AnchorGUI = null;
	public uwbAnchor(uwbInstance inst, signAnchor ui) {
		AnchorInst = inst;
		AnchorGUI = ui;
	}
	public uwbInstance getInst() {
		return AnchorInst;
	}
	public signAnchor getUI() {
		return AnchorGUI;
	}
}
