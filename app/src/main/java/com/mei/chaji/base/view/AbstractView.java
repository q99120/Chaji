package com.mei.chaji.base.view;


/**
 * View 基类
 *
 * @author quchao
 * @date 2017/11/27
 */

public interface AbstractView {


    /**
     * Show error message
     *
     * @param errorMsg error message
     */
    void showErrorMsg(String errorMsg);


    /**
     * Show error
     */
    void showError();

    /**
     * Show loading
     */
    void showLoading();

    /**
     * Reload
     */
    void reload();

    /**
     * Show login view
     */
    void showLoginView();

    /**
     * Show logout view
     */
    void showLogoutView();

    /**
     * Show collect success
     */
    void showCollectSuccess();

    /**
     * Show cancel collect success
     */
    void showCancelCollectSuccess();

    /**
     * Show toast
     *
     * @param message Message
     */
    void showToast(String message);

    /**
     * Show snackBar
     *
     * @param message Message
     */
    void showSnackBar(String message);

}
