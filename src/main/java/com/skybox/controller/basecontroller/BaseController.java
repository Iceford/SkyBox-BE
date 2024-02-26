package com.skybox.controller.basecontroller;

import com.skybox.entity.constants.Constants;
import com.skybox.entity.dto.SessionShareDto;
import com.skybox.entity.dto.SessionWebUserDto;
import com.skybox.entity.enums.ResponseCodeEnum;
import com.skybox.entity.vo.PaginationResultVO;
import com.skybox.entity.vo.ResponseVO;
import com.skybox.utils.CopyTools;
import com.skybox.utils.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.controller.basecontroller
 * @ClassName: BaseController
 * @Datetime: 2023/11/12 20:59
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 控制器类的基类，包含一些公共的方法和属性，用于在控制器中重复使用的功能
 */

public class BaseController {
    // 受保护的静态常量字符串：用于表示成功的状态
    protected static final String STATUC_SUCCESS = "success";
    // 受保护的静态常量字符串：用于表示失败的状态
    protected static final String STATUC_ERROR = "error";
    // 日志记录器：使用SLF4J库创建了一个日志记录器，用于输出日志信息
    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    /**
     * @param t
     * @return ResponseVO
     * @description 根据传入的对象创建一个成功的响应对象 ResponseVO，并设置相关属性
     */
    protected <T> ResponseVO getSuccessResponseVO(T t) {
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setStatus(STATUC_SUCCESS);
        responseVO.setCode(ResponseCodeEnum.CODE_200.getCode());
        responseVO.setInfo(ResponseCodeEnum.CODE_200.getMsg());
        responseVO.setData(t);
        return responseVO;
    }

    /**
     * @param result
     * @param clazz
     * @return PaginationResultVO<T>
     * @description 将一个泛型类型为 PaginationResultVO<S> 的分页结果对象转换为另一个泛型类型为 PaginationResultVO<T> 的分页结果对象。该方法使用 CopyTools.copyList() 方法将列表中的对象进行复制转换
     */
    protected <S, T> PaginationResultVO<T> convert2PaginationVO(PaginationResultVO<S> result, Class<T> clazz) {
        PaginationResultVO<T> resultVO = new PaginationResultVO<>();
        resultVO.setList(CopyTools.copyList(result.getList(), clazz));
        resultVO.setPageNo(result.getPageNo());
        resultVO.setPageSize(result.getPageSize());
        resultVO.setPageTotal(result.getPageTotal());
        resultVO.setTotalCount(result.getTotalCount());
        return resultVO;
    }

    /**
     * @param session
     * @return SessionWebUserDto
     * @description 从HttpSession中获取具有给定共享ID的SessionShareDto对象
     */
    protected SessionWebUserDto getUserInfoFromSession(HttpSession session) {
        SessionWebUserDto sessionWebUserDto = (SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY);
        return sessionWebUserDto;
    }

    /**
     * @param session
     * @param shareId
     * @return SessionShareDto
     * @description 从HttpSession中获取具有给定共享ID的SessionShareDto对象
     */
    protected SessionShareDto getSessionShareFromSession(HttpSession session, String shareId) {
        SessionShareDto sessionShareDto = (SessionShareDto) session.getAttribute(Constants.SESSION_SHARE_KEY + shareId);
        return sessionShareDto;
    }

    /**
     * @param response
     * @param filePath
     * @return void
     * @description 读取指定路径的文件，并将文件内容输出到HttpServletResponse中，用于文件下载等操作
     */
    protected void readFile(HttpServletResponse response, String filePath) {
        if (!StringTools.pathIsOk(filePath)) {
            return;
        }
        OutputStream out = null;
        FileInputStream in = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }
            in = new FileInputStream(file);
            byte[] byteData = new byte[1024];
            out = response.getOutputStream();
            int len = 0;
            while ((len = in.read(byteData)) != -1) {
                out.write(byteData, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            logger.error("读取文件异常", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("IO异常", e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("IO异常", e);
                }
            }
        }
    }

}

