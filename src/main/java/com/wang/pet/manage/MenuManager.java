package com.wang.pet.manage;
 
import com.wang.pet.entity.AccessToken;
import com.wang.pet.util.WeixinUtil;
import com.wang.pet.entity.AccessToken;
import com.wang.pet.entity.Button;
import com.wang.pet.entity.CommonButton;
import com.wang.pet.entity.ComplexButton;
import com.wang.pet.entity.Menu;
import com.wang.pet.util.WeixinUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
/**
 * 菜单管理器类
 * 
 * @author caoxinyu
 * @date 2013-08-08
 */
public class MenuManager {
	private static Logger log = LoggerFactory.getLogger(MenuManager.class);
 
	public static void main(String[] args) {
		// 第三方用户唯一凭证
		String appId = "wxa9d15e765cbb1b24";
		// 第三方用户唯一凭证密钥
		String appSecret = "e1d8d824ba5d73b26c7b52bbd853ef3d";
 
		// 调用接口获取access_token
		AccessToken at = WeixinUtil.getAccessToken(appId, appSecret);

		System.out.println(at);

		if (null != at) {
			// 调用接口创建菜单
			int result = WeixinUtil.createMenu(getMenu(), at.getToken());
 
			// 判断菜单创建结果
			if (0 == result)
				log.info("菜单创建成功！");
			else
				log.info("菜单创建失败，错误码：" + result);
		}
	}
 
	/**
	 * 组装菜单数据
	 * 
	 * @return
	 */
	private static Menu getMenu() {
		CommonButton btn11 = new CommonButton();
		btn11.setName("寄养申请");
		btn11.setType("click");
		btn11.setKey("11");
 
		CommonButton btn12 = new CommonButton();
		btn12.setName("寄养通知");
		btn12.setType("click");
		btn12.setKey("12");
 
		CommonButton btn13 = new CommonButton();
		btn13.setName("信息回馈");
		btn13.setType("click");
		btn13.setKey("13");
 
		CommonButton btn21 = new CommonButton();
		btn21.setName("公司简介");
		btn21.setType("click");
		btn21.setKey("21");

		CommonButton btn22 = new CommonButton();
		btn22.setName("白鼠销售");
		btn22.setType("click");
		btn22.setKey("22");

		CommonButton btn31 = new CommonButton();
		btn31.setName("闲谈小屋");
		btn31.setType("click");
		btn31.setKey("31");
 
		ComplexButton mainBtn1 = new ComplexButton();
		mainBtn1.setName("宠物寄养");
		mainBtn1.setSub_button(new CommonButton[] { btn11, btn12, btn13 });
 
		ComplexButton mainBtn2 = new ComplexButton();
		mainBtn2.setName("公司概况");
		mainBtn2.setSub_button(new CommonButton[] { btn21,btn22});

		ComplexButton mainBtn3 = new ComplexButton();
		mainBtn3.setName("闲谈小屋");
		mainBtn3.setSub_button(new CommonButton[] { btn31 });
 
		/**
		 * 这是公众号xiaoqrobot目前的菜单结构，每个一级菜单都有二级菜单项<br>
		 * 
		 * 在某个一级菜单下没有二级菜单的情况，menu该如何定义呢？<br>
		 * 比如，第三个一级菜单项不是“更多体验”，而直接是“幽默笑话”，那么menu应该这样定义：<br>
		 * menu.setButton(new Button[] { mainBtn1, mainBtn2, btn33 });
		 */
		Menu menu = new Menu();
		menu.setButton(new Button[] { mainBtn1, mainBtn2, mainBtn3 });
 
		return menu;
	}
}