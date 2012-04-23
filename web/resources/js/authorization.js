/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

function AuthorizeUser( authType )
{
    var facebookAuthURI;
    
    if( authType == "Facebook" ) {
        
        facebookAuthURI =  "https://www.facebook.com/dialog/oauth" 
                             + "?client_id=SilverTrumpets"
                             + "&redirect_uri=http://www.silvertrumpets.org/facebookauth"
                             + "&scope=publish_stream"
                             + "&state=#{agClanChannelBean.selectedChannel}";
        window.open(facebookAuthURI,
                    "Authorize",
                    "toolbar=no, location=yes, directories=no, status=yes, menubar=no, scrollbars=yes, resizable=no, copyhistory=no, width=600, height=600");
    }
    else {
        alert( "AuthType is not defined yet: " + authType );
    }
}
