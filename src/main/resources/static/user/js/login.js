($(function () {
    //设置错误信息样式
    function showCSS(){
        $(".msg-error b").css("color","red");
        $(".msg-error").css({'border-style': 'solid','border-color': '#faccc6','border-width':'thin','background-color':'#ffebeb'});
    }

    //扫码登录
    $(".login_left").click(function () {
        $(".login_code").show();
        $(".user_login").hide();
        $(".login_right .c").css({'color':'#666','font-weight':'normal'});
        $(".login_left .c").css({'color':'#F1251b','font-weight':'bold'});
    });

    //账户登录
    $(".login_right").click(function () {
        $(".user_login").show();
        $(".login_code").hide();
        $(".login_left .c").css({'color':'#666','font-weight':'normal'});
        $(".login_right .c").css({'color':'#F1251b','font-weight':'bold'});
    });

    //点击登录按钮
    $("#login_btn").click(function () {
        var name = $("#user_text").val();
        var pass = $("#pwd").val();

        //非空验证
        if((name ==""||name==null)&&(pass==""||pass==null)){
            showCSS();
            $(".msg-error b").html("请输入账户名和密码");
        }else if(pass==""||pass==null){
            showCSS();
            $(".msg-error b").html("请输入密码");
        }else if(name==""||name==null){
            showCSS();
            $(".msg-error b").html("请输入账户名");
        }else{
            $(".msg-error b").html("");

            $.ajax({
                url:"/user/login",
                type:"post",
                dataType:"json",
                data:{
                    userName:$("#user_text").val(),
                    password:$("#pwd").val()
                },
                success:function (result){
                    if(result.code == 0){
                        window.location.href="http://localhost:8080/user/index.html";
                    }else{
                        alert("error");
                    }
                },
                error:function () {
                    alert("失败")
                }
            })
        }
    })
})
)