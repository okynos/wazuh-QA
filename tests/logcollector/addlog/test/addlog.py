# test1AddLog.py
def test_AddLogAgent():
        f = open('/var/ossec/logs/ossec.log', 'r')
        assert "test1AddLog.log" in f.read()

def test_AlertInManager():
        f = open('/var/ossec/logs/alerts/alerts.log', 'r')
        assert "Jun 27 14:35:27 localhost sshd[1258]: pam_unix(sshd:session): session opened for user root by (uid=0)" in f.read()
