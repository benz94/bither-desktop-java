/*
 *
 *  Copyright 2014 http://Bither.net
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */

package net.bither.qrcode;

import net.bither.bitherj.core.Tx;
import net.bither.bitherj.qrcode.QRCodeTxTransport;
import net.bither.bitherj.qrcode.QRCodeUtil;
import net.bither.bitherj.utils.Utils;
import net.bither.utils.LocaliserUtils;

public class DesktopQRCodSend {

    public static int QRCodeSendCode = 1;
    private static byte[] lock = new byte[0];
    private java.util.List<String> contents;
    private int sendCode;
    private int currentPage;
    private String receiveMsg;


    public DesktopQRCodSend(Tx tx, String changeAddress, int signingIndex) {
        synchronized (lock) {
            this.sendCode = QRCodeSendCode;
            String codeString = QRCodeTxTransport.getPresignTxString(tx, changeAddress,
                    LocaliserUtils.getString("address_cannot_be_parsed"), signingIndex, QRCodeTxTransport.TxTransportType.DesktopHDM);
            this.contents = QRCodeUtil.getQrCodeStringList(QRCodeUtil.encodeQrCodeString(codeString));
            this.currentPage = 0;
            QRCodeSendCode++;
        }

    }

    public DesktopQRCodSend(String codeString) {
        synchronized (lock) {
            this.sendCode = QRCodeSendCode;
            this.contents = QRCodeUtil.getQrCodeStringList(QRCodeUtil.encodeQrCodeString(codeString));
            this.currentPage = 0;
            QRCodeSendCode++;
        }

    }


    public boolean sendComplete() {
        String[] headers = new String[]{Integer.toString(sendCode),
                Integer.toString(contents.size() - 1), Integer.toString(currentPage)};
        String sendHeader = Utils.joinString(headers, QRCodeUtil.QR_CODE_SPLIT);
        return Utils.compareString(sendHeader, receiveMsg);
    }

    public boolean allComplete() {
        if (sendComplete()) {
            return currentPage == this.contents.size() - 1;
        }
        return false;
    }

    public String getShowMessage() {
        String msg = "";
        String[] headers = new String[]{Integer.toString(sendCode),
                Integer.toString(contents.size() - 1), Integer.toString(currentPage)};
        String sendHeader = Utils.joinString(headers, QRCodeUtil.QR_CODE_SPLIT);
        if (this.contents.size() == 1) {
            msg = sendHeader + QRCodeUtil.QR_CODE_SPLIT + this.contents.get(0);
        } else {
            if (currentPage < this.contents.size()) {
                msg = Integer.toString(sendCode) + QRCodeUtil.QR_CODE_SPLIT + this.contents.get(currentPage);
                currentPage++;
            }

        }
        return msg;
    }

    public void setReceiveMsg(String msg) {
        this.receiveMsg = msg;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DesktopQRCodSend) {
            DesktopQRCodSend other = (DesktopQRCodSend) obj;
            return sendCode == other.sendCode;

        }
        return false;
    }
}