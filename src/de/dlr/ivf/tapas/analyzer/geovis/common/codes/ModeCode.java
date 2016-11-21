package de.dlr.ivf.tapas.analyzer.geovis.common.codes;

public enum ModeCode {

	WALK(0),
	BIKE(1),
	MIV(2),
	MIV_PASS(3),
	CAP(4),
	PT(5),
	TRAIN(6);
	
	private int code = 0;
	
	private ModeCode(int code){
		this.code = code;
	}
	
	public static ModeCode findModeCode(int modeCode){
		for(ModeCode mode : ModeCode.values()){
			if(mode.getCode() == modeCode){
				return mode;
			}
		}
		return null;
	}
	
	public int getCode() {
		return code;
	}
		
	
}
