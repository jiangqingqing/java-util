package jqq.ua;


class SimpleLineRule extends AbstractUARule {

	public SimpleLineRule(UserAgent.OS os, String keyword, int maxLength, int[] indexList, String spliter) {
		super(Category.LINE_SIMPLE, os, keyword, maxLength, indexList, spliter);
	}

	public SimpleLineRule(UserAgent.OS os, String keyword, int maxLength, int[] indexList) {
		super(Category.LINE_SIMPLE, os, keyword, maxLength, indexList);
	}

	@Override
	public UserAgent match(String userAgent) {
		String tempUA = userAgent;
		String searchWord = keyword;
		if (super.lower) {
			tempUA = userAgent.toLowerCase();
			searchWord = searchWord.toLowerCase();
		}
		if (tempUA.startsWith(searchWord)) {
			return new UserAgent(os, userAgent.split(spliter)[0]);
		}
		return null;
	}
}