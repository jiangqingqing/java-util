package jqq.cache;

/**
 * 缓存
 * <br>==========================
 * <br> 公司：阿里互动娱乐
 * <br> 系统：九游游戏中心客户端后台
 * <br> 开发：qq.jiang(jqq105718@alibaba-inc.com)
 * <br> 创建时间：2019/10/19 上午11:06
 * <br>==========================
 */
public interface Cache {

    Object get(String key);

    void put(String key,String object,Integer expireTime);

    void clean(String key);

    void delete(String key);

}
