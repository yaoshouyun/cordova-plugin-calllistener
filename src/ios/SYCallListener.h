#import <UIKit/UIKit.h>
#import <Cordova/CDVPlugin.h>
#import <CoreTelephony/CTCallCenter.h>
#import <CoreTelephony/CTCall.h>

// simple object to keep track of call record information
@interface CallRecord : NSObject {
    int duration;
    NSDate* startDate;
    NSDate* endDate;
    NSString* callID;
}

@property (nonatomic, assign) int duration;
@property (nonatomic, strong) NSDate* startDate;
@property (nonatomic, strong) NSDate* endDate;
@property (nonatomic, copy) NSString* callID;
@end

@interface SYCallListener : CDVPlugin

@property (nonatomic, strong) CTCallCenter *objCallCenter;
@property (nonatomic, strong) CallRecord *record;

- (void)addListener:(CDVInvokedUrlCommand*)command;
- (void)getCallInfo:(CDVInvokedUrlCommand*)command;

@end
