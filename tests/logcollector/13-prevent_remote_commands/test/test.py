# test1AddLog.py
def test_agent():
        f = open('/var/ossec/logs/ossec.log', 'r')
        assert "Monitoring output of command(10): ls -l /13-prevent_remote_commands/ls_test_dir/ --time-style=+" in f.read()
        f.close()
        f = open('/var/ossec/logs/ossec.log', 'r')
        assert "Monitoring full output of command(10): cat /13-prevent_remote_commands/test.conf" in f.read()
        f.close()

def test_manager():
        f = open('/var/ossec/logs/archives/archives.json', 'r')
        assert "ossec: output: 'ls -l /13-prevent_remote_commands/ls_test_dir/ --time-style=+': total 4" in f.read()
        f.close()
        f = open('/var/ossec/logs/archives/archives.json', 'r')
        assert "ossec: output: 'ls -l /13-prevent_remote_commands/ls_test_dir/ --time-style=+': -rw-r--r-- 1 root root 396  test.conf" in f.read()
        f.close()
        f = open('/var/ossec/logs/archives/archives.json', 'r')
        assert "ossec: output: 'cat /13-prevent_remote_commands/test.conf':\n<ossec_config>\n\n  <localfile>\n    <log_format>command</log_format>\n    <command>ls -l /13-prevent_remote_commands/ls_test_dir/ --time-style=+</command>\n    <frequency>10</frequency>\n  </localfile>\n\n  <localfile>\n    <log_format>full_command</log_format>\n    <command>cat /13-prevent_remote_commands/test.conf</command>\n    <frequency>10</frequency>\n  </localfile>\n\n</ossec_config>","decoder":{"name":"ossec"},"location":"cat /13-prevent_remote_commands/test.conf" in f.read()
        f.close()
