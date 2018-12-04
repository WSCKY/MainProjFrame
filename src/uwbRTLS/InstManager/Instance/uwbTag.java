package uwbRTLS.InstManager.Instance;

import uwbRTLS.uiComponent.uiTag;

public class uwbTag {
	private uwbInstance TagInst = null;
	private uiTag TagGUI = null;
	public uwbTag(uwbInstance inst, uiTag ui) {
		TagInst = inst;
		TagInst.setType(uwbInstance.TAG);
		TagGUI = ui;
	}
	public uwbInstance getInst() {
		return TagInst;
	}
	public uiTag getUI() {
		return TagGUI;
	}
}
