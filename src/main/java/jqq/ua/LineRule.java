package jqq.ua;


class LineRule extends AbstractUARule {

	public LineRule(UserAgent.OS os, String keyword, int maxLength, int[] indexList, String spliter) {
		super(Category.LINE, os, keyword, maxLength, indexList, spliter);
	}

	public LineRule(UserAgent.OS os, String keyword, int maxLength, int[] indexList) {
		super(Category.LINE, os, keyword, maxLength, indexList);
	}

	@Override
	public UserAgent match(String userAgent) {
		String tempUA = userAgent;
		String searchWord = keyword;
		if (super.lower) {
			tempUA = userAgent.toLowerCase();
			searchWord = searchWord.toLowerCase();
		}
		if (!tempUA.startsWith(searchWord)) {
			return null;
		}
		int leftBracket = tempUA.indexOf("(");
		int rightBracket = tempUA.indexOf(")");
		if (leftBracket < 0 || rightBracket < 0) {
			return null;
		}
		tempUA = userAgent.substring(leftBracket+1, rightBracket);
		String[] items = tempUA.split(spliter);
		if (items.length >= maxLength) {
			return new UserAgent(os, items[indexList[0]].trim());
		}
		return null;
	}
}