package tiles;
import tiles.Tile;

public class Portal extends Tile{
	private String targetMap;
	@Override
	public void Initialize(String...strings) {
		super.Initialize(strings);
		targetMap = strings[4];
	}
	public String getTargetMap(){
		return targetMap;
	}
	@Override
	public String[] generateParameters() {
		String[] para = new String[5];
		para[0] = currentImage + "";
		if(walkAble) {
			para[1] = "walkable";
		}else {
			para[1] = "unwalkable";
		}
		if(getSeen()) {
			para[2] = "seen";
		}else {
			para[2] = "unseen";
		}
		if(getTransparent()) {
			para[3] = "transparent";
		}else {
			para[3] = "nottransparent";
		}
		para[4] = targetMap;
		return para;
	}
}
