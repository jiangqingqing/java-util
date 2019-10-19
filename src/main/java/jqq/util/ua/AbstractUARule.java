package jqq.util.ua;


abstract class AbstractUARule implements UARule {

	final Category category;
	final UserAgent.OS os;
	final String keyword;
	final int maxLength;
	final int[] indexList;
	String spliter = ";";
	boolean lower = true;

	public AbstractUARule(Category category, UserAgent.OS os, String keyword, int maxLength, int[] indexList, String spliter, boolean lower) {
		super();
		this.os = os;
		this.category = category;
		this.keyword = keyword;
		this.maxLength = maxLength;
		this.indexList = indexList;
		this.spliter = spliter;
		this.lower = lower;
	}

	public AbstractUARule(Category category, UserAgent.OS os, String keyword, int maxLength, int[] indexList, String spliter) {
		super();
		this.os = os;
		this.category = category;
		this.keyword = keyword;
		this.maxLength = maxLength;
		this.indexList = indexList;
		this.spliter = spliter;
	}

	public AbstractUARule(Category category, UserAgent.OS os, String keyword, int maxLength, int[] indexList) {
		super();
		this.os = os;
		this.category = category;
		this.keyword = keyword;
		this.maxLength = maxLength;
		this.indexList = indexList;
	}

	@Override
	public Category category() {
		return this.category;
	}

	protected UserAgent process_inner(String userAgent) {
		String[] token = userAgent.split(this.spliter);
		if (token.length < this.maxLength) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		String comma = "";
		for (int idx=0; idx<indexList.length; idx++) {
			if (indexList[idx] >= token.length) {
				return null;
			}
			result.append(comma).append(token[indexList[idx]]);
			comma = ",";
		}
		return new UserAgent(this.os, result.toString());
	}
}