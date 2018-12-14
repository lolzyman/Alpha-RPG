
public class Door extends Tile{
	private boolean locked = true;
	private String[] keyIDs;
	public Door() {
		
	}
	@Override
	public void Initialize(String...strings) {
		super.Initialize(strings);
		if(strings[4].equals("locked")) {
			locked = true;
		}else {
			locked = false;
		}
		// Takes the rest of the parameters and assumes that they are keys
		if(strings.length > 5) {
			keyIDs = new String[strings.length - 5];
			int i = 5;
			do {
				keyIDs[i-5] = strings[i];
				i++;
			}while(i < strings.length);
		}
	}
	public Door(String...strings) {
		
	}
	@Override
	public void interact(Entity being) {
		if(!locked) {
			this.currentImage = 0;
			this.walkAble = true;
		}
		if(keyIDs != null) {
			for(String keyId:keyIDs) {
				for(Item theirKey:being.getInventory().getKeys()) {
					if(theirKey.getID().equals(keyId)) {
						if(locked) {
							locked = false;
							//The next to lines of code are for if the door opens immediatle or if the door just unlocks
							//If the door opens immediatly, then include them
							//If the door only unlocks, then remove them
							this.currentImage = 0;
							this.walkAble = true;
						}else {
							locked = true;
						}
					}
				}
			}
		}
	}
	@Override
	public String[] generateParameters() {
		String[] para;
		if(keyIDs != null) {
			para = new String[5 + keyIDs.length];
			for(int i = 0;i < keyIDs.length;i++) {
				para[5+i] = keyIDs[i];
			}
		}else {
			para = new String[5];
		}
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
		}if(locked) {
			para[4] = "locked";
		}else {
			para[4] = "unlocked";
		}
		return para;
	}
}
