
from websocket_server import WebsocketServer
from gpiozero import DigitalOutputDevice, DigitalInputDevice
from threading import Thread, Event
from signal import signal, SIGINT
import sys
import time

# Devices setup (all assumed active LOW except boiler)
# grinder = DigitalOutputDevice(27, active_high=False, initial_value=False)
# boiler = DigitalOutputDevice(22, active_high=False, initial_value=False)
# motor_breaker = DigitalOutputDevice(23, active_high=False, initial_value=False)
# pulper = DigitalOutputDevice(24, active_high=False, initial_value=False)
# conveyor_motor = DigitalOutputDevice(
#     25, active_high=False, initial_value=False)
# heater = DigitalOutputDevice(
#     5, active_high=False, initial_value=False)  # Heater pin setup
# ir_sensor = DigitalInputDevice(17)

cycle_running = Event()

# GLOBAL SERVER
server = None


def shutdown_all_devices():
    print("\nShutting down... turning all components OFF.")
    # grinder.off()
    # boiler.off()
    # motor_breaker.off()
    # pulper.off()
    # conveyor_motor.off()
    # heater.off()


def shutdown_handler(*args):
    shutdown_all_devices()
    sys.exit(1)


def pulper_process(duration=60):
    print(f"\nStarting pulper for {duration} seconds...")
    try:
        # pulper.on()
        for remaining in range(duration, 0, -1):
            print(f"Pulper time left: {remaining} seconds", end='\r')
            time.sleep(1)
    finally:
        # pulper.off()
        print("\nPulper process complete. open the lid manually")

    conveyor_process()


def conveyor_process(duration=30):
    print(f"Starting conveyor motor for {duration} seconds...")
    try:
        # conveyor_motor.on()
        for remaining in range(duration, 0, -1):
            print(f"Conveyor time left: {remaining} seconds", end='\r')
            time.sleep(1)
    finally:
        # conveyor_motor.off()
        print("\nConveyor motor stopped.")

    # After first conveyor run, start heater
    heater_process()


def heater_process(duration=180):
    print(f"Starting heater for {duration} seconds...")
    try:
        # heater.on()
        for remaining in range(duration, 0, -1):
            print(f"Heater time left: {remaining} seconds", end='\r')
            time.sleep(1)
    finally:
        # heater.off()
        print("\nHeater stopped.")

    # After heater finishes, run conveyor AGAIN for 60 seconds
    conveyor_process_after_heater()


def conveyor_process_after_heater(duration=60):
    print(f"Starting conveyor motor again for {duration} seconds...")
    try:
        # conveyor_motor.on()
        for remaining in range(duration, 0, -1):
            print(
                f"Conveyor time left (2nd run): {remaining} seconds", end='\r')
            time.sleep(1)
    finally:
        # conveyor_motor.off()
        print("\nSecond conveyor run stopped.")

    # Now stop everything and unlock cycle
    shutdown_all_devices()
    cycle_running.clear()
    print("Process cycle complete. Ready for next IR detection.")


def motor_breaker_process(duration=20):
    print(f"Starting motor breaker for {duration} seconds...")
    try:
        # motor_breaker.on()
        for remaining in range(duration, 0, -1):
            print(f"Motor breaker time left: {remaining} seconds", end='\r')
            time.sleep(1)
    finally:
        # motor_breaker.off()
        print("\nMotor breaker stopped.")

    pulper_process()


def boiling_process(duration=10):
    print(f"Starting boiling for {duration} seconds...")
    try:
        # boiler.on()
        for remaining in range(duration, 0, -1):
            print(f"Boiling time left: {remaining} seconds", end='\r')
            time.sleep(1)
    finally:
        # boiler.off()
        print("\nBoiling stopped. open the lid manually")

    motor_breaker_process()


def grinder_process(duration=20):
    print("IR detected! Starting full process cycle.")

    if cycle_running.is_set():
        print("Cycle already running, ignoring.")
        return

    cycle_running.set()
    shutdown_all_devices()  # Ensure all devices off before starting

    try:
        print(f"Starting grinder for {duration} seconds...")
        # grinder.on()
        for remaining in range(duration, 0, -1):
            print(f"Grinding time left: {remaining} seconds", end='\r')
            time.sleep(1)
    finally:
        # grinder.off()
        print("\nGrinder stopped.")

    boiling_process()


def ir_detected_none():
    print(">> IR Sensor Not detecting")
    if server:
        server.send_message_to_all("ir-data: true")


def ir_detected():
    print(">> IR Sensor Triggered")
    if server:
        server.send_message_to_all("ir-data: true")

    # HIWALAY NA TO
    # if not cycle_running.is_set():
    #     print(">> Starting new process thread")
    #     thread = Thread(target=grinder_process)
    #     thread.start()
    # else:
    #     print(">> Cycle already running; ignoring this detection")


def start_grinder_process():
    if not cycle_running.is_set():
        print(">> Starting new process thread")
        thread = Thread(target=grinder_process)
        thread.start()
    else:
        print(">> Cycle already running; ignoring this detection")


def ws_message_received(client, server, message):
    if "mobile-data: start-grinder":
        print(">>> START GRINDER")
        start_grinder_process()
    print(message)


# WEBSOCKET SETUP
PORT = 8765


def start_websocket():
    global server
    server = WebsocketServer(host="0.0.0.0", port=PORT)
    server.set_fn_message_received(ws_message_received)
    server.serve_forever()


def main():
    signal(SIGINT, shutdown_handler)
    # ir_sensor.when_activated = ir_detected
    # ir_sensor.when_deactivated = ir_detected_none

    print("Starting websocket...")
    websocket_thread = Thread(target=start_websocket)
    websocket_thread.daemon = True
    websocket_thread.start()
    print(f"Websocket server started at port {PORT}")

    while True:
        pass


if __name__ == "__main__":
    main()
