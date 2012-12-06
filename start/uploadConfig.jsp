<html>

<head>
<title>Upload a configuration file</title>
<%@ include file="index.jsp" %>
<br>
<div align="center">
    <div class="mainaddnew">
      <form action="uploadDone.jsp" method="post" enctype="multipart/form-data">
      <br>
            <table border="0" cellspacing="1" cellpadding="1" width="98%">
                <tr>
                    <td align="center" class="sectionname" colspan="2">Upload an existing configuration file</td>
                </tr>
                <tr><td>

              <tr>
                  <td style="text-align:center;">
                      <input type="file" name="content" size="50">
                  </td>
              </tr>
              <tr><td>&nbsp;</td></tr><tr>
                  <td style="text-align:center;">
                      <input type="submit" name="submit" value="Upload" />
                  </td>

              </tr>
          </table>
      </form>

        <p class="last">&nbsp;</p>

    </div>
</div>

      
  </center>

  </div>


<br/>
    </body>
</html>
