def test_agent():
        f = open('/var/ossec/logs/ossec.log', 'r')
        assert "ossec-syscheckd: INFO: Directory set for real time monitoring: '/add_file_alert'." in f.read()
        f.close()

def test_manager():
        f = open('/var/ossec/logs/alerts/alerts.log', 'r')
        assert "New file '/add_file_alert/add_file_alert.txt' added to the file system." in f.read()
        f.close()
