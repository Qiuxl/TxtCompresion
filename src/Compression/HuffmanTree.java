package Compression;

/**
 * 哈夫曼树
 * @author andy
 *
 */
public class HuffmanTree {
	public HuffmanTree left;
	public HuffmanTree right;
	public Object value;
	public int weight;//权重
	public HuffmanTree(HuffmanTree left, HuffmanTree right, Object value,
			int weight) {
		super();
		this.left = left;
		this.right = right;
		this.value = value;
		this.weight = weight;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + weight;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HuffmanTree other = (HuffmanTree) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		if (weight != other.weight)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "HuffmanTree [left=" + left + ", right=" + right + ", value="
				+ value + ", weight=" + weight + "]\n";
	}
	
	
}
