//
//  ILScheduleXMLParserDelegate.h
//  Inner Life
//
//  Created by Matthew Bates on 13/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ILSchedule.h"

@interface ILScheduleXMLParserDelegate : NSObject <NSXMLParserDelegate>
{
    @private
    NSMutableArray *_currentSession;
}

@property ILSchedule *schedule;

@end
