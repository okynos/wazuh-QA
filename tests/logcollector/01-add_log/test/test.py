# test1AddLog.py
def test_agent():
        f = open('/var/ossec/logs/ossec.log', 'r')
        assert "ssh.log" in f.read()

def test_manager():
        f = open('/var/ossec/logs/alerts/alerts.log', 'r')
        assert "Jun 27 14:35:27 localhost sshd[1258]: pam_unix(sshd:session): session opened for user root by (uid=0)" in f.read()
