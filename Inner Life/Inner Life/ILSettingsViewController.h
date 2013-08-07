//
//  ILSettingsViewController.h
//  Inner Life
//
//  Created by Matthew Bates on 07/08/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ILTimeSelector.h"
#import "ILAppManager.h"
#import "DURoundedBorderLabel.h"

@interface ILSettingsViewController : UIViewController <ILTimeSelectorDelegate>
{
    @private
    DURoundedBorderLabel *lblTimeRangeDescription;
    ILTimeSelector *timeSelector;
    
    NSInteger amTime;
    NSInteger pmTime;
}

@end
